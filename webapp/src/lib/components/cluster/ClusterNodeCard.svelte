<script lang="ts">
    import * as Card from '@/ui/card'
    import { Badge } from '@/ui/badge'
    import * as Tooltip from '@/ui/tooltip'
    import type { ClusterNode } from '$lib/data/types'
    import { cluster } from '$lib/data/cluster'
    import Crown from '@lucide/svelte/icons/crown'
    import Radio from '@lucide/svelte/icons/radio'
    import CircleAlert from '@lucide/svelte/icons/circle-alert'

    let { node }: { node: ClusterNode } = $props()

    const formatUs = (us: number, signed = false) => {
        const sign = signed && us > 0 ? '+' : ''
        if (Math.abs(us) >= 1000) return `${sign}${(us / 1000).toFixed(2)} ms`
        return `${sign}${us} µs`
    }

    let mismatched = $derived(node.framerate !== $cluster.framerate)
    let offline = $derived(!node.inView || node.stale)
</script>

<Card.Root class={offline ? 'opacity-60' : ''}>
    <Card.Header>
        <Card.Title class="flex items-center gap-2">
            <span class="truncate font-mono">{node.name}</span>
            {#if node.self}
                <Badge variant="outline">you</Badge>
            {/if}
        </Card.Title>
        <Card.Description>
            {node.master ? 'Owns the show clock' : 'Chases the master clock'}
        </Card.Description>
        <Card.Action>
            {#if node.master}
                <Badge variant="default"><Crown data-icon="inline-start" />Master</Badge>
            {:else}
                <Badge variant="secondary">Follower</Badge>
            {/if}
        </Card.Action>
    </Card.Header>

    <Card.Content class="flex flex-col gap-4">
        <div class="flex flex-wrap items-center gap-2">
            {#if node.playing}
                <Badge variant="success"><Radio data-icon="inline-start" />Playing</Badge>
            {:else}
                <Badge variant="outline">Idle</Badge>
            {/if}

            {#if node.stale}
                <Tooltip.Provider>
                    <Tooltip.Root>
                        <Tooltip.Trigger>
                            <Badge variant="warning">
                                <CircleAlert data-icon="inline-start" />
                                Stale
                            </Badge>
                        </Tooltip.Trigger>
                        <Tooltip.Content>
                            <p>Still in the cluster view but has stopped reporting in.</p>
                        </Tooltip.Content>
                    </Tooltip.Root>
                </Tooltip.Provider>
            {:else if !node.inView}
                <Badge variant="destructive">Left</Badge>
            {/if}

            <Badge variant={mismatched ? 'warning' : 'outline'}>{node.framerate} fps</Badge>
        </div>

        <div class="flex flex-col gap-1.5">
            <span class="text-muted-foreground text-xs uppercase">Drives</span>
            <div class="flex flex-wrap gap-1">
                {#each node.outputs as output (output)}
                    <Badge variant="secondary">{output}</Badge>
                {:else}
                    <span class="text-muted-foreground text-sm">nothing</span>
                {/each}
            </div>
        </div>

        <div class="flex flex-col gap-1.5">
            <span class="text-muted-foreground text-xs uppercase">Clock</span>
            {#if node.master}
                <span class="text-sm">Reference — every other node aligns to this one.</span>
            {:else if !node.synced}
                <Badge variant="warning">Not synced yet</Badge>
            {:else}
                <div class="grid grid-cols-2 gap-2 font-mono text-sm tabular-nums">
                    <Tooltip.Provider>
                        <Tooltip.Root>
                            <Tooltip.Trigger class="flex flex-col items-start">
                                <span class="text-muted-foreground text-xs">offset</span>
                                <span>{formatUs(node.offsetUs, true)}</span>
                            </Tooltip.Trigger>
                            <Tooltip.Content>
                                <p>Correction from this node's clock to the master's.</p>
                            </Tooltip.Content>
                        </Tooltip.Root>
                    </Tooltip.Provider>
                    <Tooltip.Provider>
                        <Tooltip.Root>
                            <Tooltip.Trigger class="flex flex-col items-start">
                                <span class="text-muted-foreground text-xs">delay</span>
                                <span>{formatUs(node.delayUs)}</span>
                            </Tooltip.Trigger>
                            <Tooltip.Content>
                                <p>Measured one-way network delay to the master.</p>
                            </Tooltip.Content>
                        </Tooltip.Root>
                    </Tooltip.Provider>
                </div>
            {/if}
        </div>
    </Card.Content>
</Card.Root>
