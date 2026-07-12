<script lang="ts">
    import * as Tabs from '@/ui/tabs'
    import { Badge } from '@/ui/badge'
    import { Separator } from '@/ui/separator'
    import ControlTab from '@/ControlTab.svelte'
    import ClusterPanel from '@/cluster/ClusterPanel.svelte'
    import LogsPanel from '@/logs/LogsPanel.svelte'
    import ThemeToggle from '@/ThemeToggle.svelte'
    import { cluster, selfNode } from '$lib/data/cluster'
    import { playing, paused } from '$lib/data/control'
    import SlidersHorizontal from '@lucide/svelte/icons/sliders-horizontal'
    import Network from '@lucide/svelte/icons/network'
    import ScrollText from '@lucide/svelte/icons/scroll-text'
    import Crown from '@lucide/svelte/icons/crown'
    import Radio from '@lucide/svelte/icons/radio'

    let nodeName = $derived($selfNode?.name ?? 'standalone')
    let warnings = $derived($cluster.warnings.length)
</script>

<div class="flex min-h-screen flex-col">
    <header class="bg-card sticky top-0 z-10 border-b">
        <div class="flex flex-wrap items-center gap-x-4 gap-y-2 px-4 py-3 sm:px-6">
            <div class="flex flex-col">
                <h1 class="text-sm font-semibold tracking-[0.2em] uppercase">ShowManager</h1>
                <p class="text-muted-foreground font-mono text-xs">{nodeName}</p>
            </div>

            <Separator orientation="vertical" class="hidden h-8 sm:block" />

            <div class="flex flex-wrap items-center gap-2">
                {#if $playing}
                    <Badge variant="success"><Radio data-icon="inline-start" />Playing</Badge>
                {:else if $paused}
                    <Badge variant="warning">Paused</Badge>
                {:else}
                    <Badge variant="outline">Stopped</Badge>
                {/if}

                {#if $cluster.enabled}
                    {#if $cluster.master}
                        <Badge variant="default"><Crown data-icon="inline-start" />Master</Badge>
                    {:else}
                        <Badge variant="secondary">Follower</Badge>
                    {/if}
                    <Badge variant={$cluster.connected ? 'outline' : 'destructive'}>
                        {$cluster.nodes.length}
                        {$cluster.nodes.length === 1 ? 'node' : 'nodes'}
                    </Badge>
                {/if}
            </div>

            <div class="ml-auto">
                <ThemeToggle />
            </div>
        </div>
    </header>

    <main class="flex-1 p-4 sm:p-6">
        <Tabs.Root value="control">
            <Tabs.List>
                <Tabs.Trigger value="control">
                    <SlidersHorizontal data-icon="inline-start" />
                    Control
                </Tabs.Trigger>
                <Tabs.Trigger value="cluster">
                    <Network data-icon="inline-start" />
                    Cluster
                    {#if warnings > 0}
                        <Badge variant="warning">{warnings}</Badge>
                    {/if}
                </Tabs.Trigger>
                <Tabs.Trigger value="logs">
                    <ScrollText data-icon="inline-start" />
                    Logs
                </Tabs.Trigger>
            </Tabs.List>

            <Tabs.Content value="control">
                <ControlTab />
            </Tabs.Content>
            <Tabs.Content value="cluster">
                <ClusterPanel />
            </Tabs.Content>
            <Tabs.Content value="logs">
                <LogsPanel />
            </Tabs.Content>
        </Tabs.Root>
    </main>
</div>
