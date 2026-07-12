<script lang="ts">
    import * as Collapsible from '@/ui/collapsible'
    import { Button } from '@/ui/button'
    import type { LogEntry, LogLevel } from '$lib/data/types'
    import { cn } from '$utils'
    import ChevronRight from '@lucide/svelte/icons/chevron-right'

    let { entry }: { entry: LogEntry } = $props()

    const levelColors: Record<LogLevel, string> = {
        ERROR: 'text-destructive',
        WARN: 'text-warning',
        INFO: 'text-foreground',
        DEBUG: 'text-muted-foreground',
        TRACE: 'text-muted-foreground'
    }
</script>

<div
    class={cn(
        'hover:bg-muted/60 flex flex-col px-3 py-0.5 font-mono text-xs',
        entry.level === 'ERROR' && 'bg-destructive/5',
        entry.level === 'WARN' && 'bg-warning/5'
    )}
>
    <div class="flex gap-2">
        <span class="text-muted-foreground shrink-0 tabular-nums">{entry.time}</span>
        <span class={cn('w-11 shrink-0 font-semibold', levelColors[entry.level])}>
            {entry.level}
        </span>
        <span class="text-muted-foreground w-40 shrink-0 truncate max-md:hidden">
            {entry.logger}
        </span>
        <span class="break-all whitespace-pre-wrap">{entry.message}</span>
    </div>

    {#if entry.throwable}
        <Collapsible.Root>
            <Collapsible.Trigger>
                {#snippet child({ props })}
                    <Button
                        {...props}
                        variant="ghost"
                        size="sm"
                        class="text-muted-foreground h-6 w-fit"
                    >
                        <ChevronRight data-icon="inline-start" />
                        Stack trace
                    </Button>
                {/snippet}
            </Collapsible.Trigger>
            <Collapsible.Content>
                <pre
                    class="text-muted-foreground overflow-x-auto border-l-2 py-1 pl-3 text-[11px]">{entry.throwable}</pre>
            </Collapsible.Content>
        </Collapsible.Root>
    {/if}
</div>
