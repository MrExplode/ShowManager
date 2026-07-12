<script lang="ts">
    import * as Card from '@/ui/card'
    import * as Empty from '@/ui/empty'
    import * as ToggleGroup from '@/ui/toggle-group'
    import * as InputGroup from '@/ui/input-group'
    import { ScrollArea } from '@/ui/scroll-area'
    import { Button } from '@/ui/button'
    import { Switch } from '@/ui/switch'
    import { Label } from '@/ui/label'
    import { toast } from 'svelte-sonner'
    import { logs, clearLogs } from '$lib/data/control'
    import type { LogEntry, LogLevel } from '$lib/data/types'
    import LogRow from '@/logs/LogRow.svelte'
    import Search from '@lucide/svelte/icons/search'
    import Copy from '@lucide/svelte/icons/copy'
    import Trash2 from '@lucide/svelte/icons/trash-2'
    import ScrollText from '@lucide/svelte/icons/scroll-text'

    const severity: Record<LogLevel, number> = { ERROR: 0, WARN: 1, INFO: 2, DEBUG: 3, TRACE: 4 }
    const floors = [
        { value: 'TRACE', label: 'All' },
        { value: 'INFO', label: 'Info' },
        { value: 'WARN', label: 'Warn' },
        { value: 'ERROR', label: 'Error' }
    ]

    let floor = $state<string>('TRACE')
    let query = $state('')
    let autoscroll = $state(true)
    let viewport = $state<HTMLElement | null>(null)

    let filtered = $derived(
        $logs.filter((entry: LogEntry) => {
            if (severity[entry.level] > severity[floor as LogLevel]) return false
            if (query.trim() === '') return true
            const needle = query.toLowerCase()
            return (
                entry.message.toLowerCase().includes(needle) ||
                entry.logger.toLowerCase().includes(needle)
            )
        })
    )

    $effect(() => {
        const count = filtered.length
        if (!autoscroll || !viewport || count === 0) return
        viewport.scrollTop = viewport.scrollHeight
    })

    const copy = async () => {
        const text = filtered
            .map((entry) => `[${entry.time}] [${entry.level}] ${entry.logger}: ${entry.message}`)
            .join('\n')
        await navigator.clipboard.writeText(text)
        toast.success(`Copied ${filtered.length} log ${filtered.length === 1 ? 'line' : 'lines'}`)
    }
</script>

<Card.Root>
    <Card.Header>
        <Card.Title>Logs</Card.Title>
        <Card.Description>
            Live from the backend · showing {filtered.length} of {$logs.length}
        </Card.Description>
        <Card.Action>
            <div class="flex items-center gap-2">
                <Switch id="autoscroll" bind:checked={autoscroll} />
                <Label for="autoscroll">Follow</Label>
            </div>
        </Card.Action>
    </Card.Header>

    <Card.Content class="flex flex-col gap-4">
        <div class="flex flex-wrap items-center gap-2">
            <InputGroup.Root class="max-w-xs">
                <InputGroup.Addon>
                    <Search />
                </InputGroup.Addon>
                <InputGroup.Input placeholder="Filter messages…" bind:value={query} />
            </InputGroup.Root>

            <ToggleGroup.Root type="single" bind:value={floor} variant="outline">
                {#each floors as level (level.value)}
                    <ToggleGroup.Item value={level.value}>{level.label}</ToggleGroup.Item>
                {/each}
            </ToggleGroup.Root>

            <div class="ml-auto flex gap-2">
                <Button variant="outline" size="sm" onclick={copy} disabled={filtered.length === 0}>
                    <Copy data-icon="inline-start" />
                    Copy
                </Button>
                <Button
                    variant="outline"
                    size="sm"
                    onclick={clearLogs}
                    disabled={$logs.length === 0}
                >
                    <Trash2 data-icon="inline-start" />
                    Clear
                </Button>
            </div>
        </div>

        {#if filtered.length === 0}
            <Empty.Root class="border">
                <Empty.Header>
                    <Empty.Media variant="icon">
                        <ScrollText />
                    </Empty.Media>
                    <Empty.Title>Nothing to show</Empty.Title>
                    <Empty.Description>
                        {$logs.length === 0
                            ? 'No log entries have arrived yet.'
                            : 'No entries match the current filter.'}
                    </Empty.Description>
                </Empty.Header>
            </Empty.Root>
        {:else}
            <ScrollArea class="bg-muted/30 h-[60vh] border" bind:viewportRef={viewport}>
                <div class="flex flex-col py-1">
                    {#each filtered as entry (entry.id)}
                        <LogRow {entry} />
                    {/each}
                </div>
            </ScrollArea>
        {/if}
    </Card.Content>
</Card.Root>
