<script lang="ts">
    import * as Card from '@/ui/card'
    import * as Empty from '@/ui/empty'
    import * as Alert from '@/ui/alert'
    import { Badge } from '@/ui/badge'
    import { Separator } from '@/ui/separator'
    import { cluster, syncCluster } from '$lib/data/cluster'
    import ClusterNodeCard from '@/cluster/ClusterNodeCard.svelte'
    import Network from '@lucide/svelte/icons/network'
    import TriangleAlert from '@lucide/svelte/icons/triangle-alert'
    import Unplug from '@lucide/svelte/icons/unplug'

    // the websocket only pushes structural changes; poll to keep the clock figures live
    $effect(() => {
        syncCluster()
        const id = setInterval(syncCluster, 2000)
        return () => clearInterval(id)
    })

    let state = $derived($cluster)
</script>

{#if !state.enabled}
    <Empty.Root>
        <Empty.Header>
            <Empty.Media variant="icon">
                <Network />
            </Empty.Media>
            <Empty.Title>Clustering is off</Empty.Title>
            <Empty.Description>
                This node runs standalone and owns every output. Enable clustering in
                <code class="font-mono">ShowManager/config.json</code>
                under <code class="font-mono">clusterConfig</code> to run several synced nodes, then restart.
            </Empty.Description>
        </Empty.Header>
    </Empty.Root>
{:else}
    <div class="flex flex-col gap-4">
        <Card.Root>
            <Card.Header>
                <Card.Title>{state.clusterName}</Card.Title>
                <Card.Description>
                    Discovery over {state.useMulticast ? 'multicast' : 'TCP seed nodes'} · this node runs
                    at {state.framerate} fps
                </Card.Description>
                <Card.Action>
                    {#if state.connected}
                        <Badge variant="success">Connected</Badge>
                    {:else}
                        <Badge variant="destructive">
                            <Unplug data-icon="inline-start" />
                            Disconnected
                        </Badge>
                    {/if}
                </Card.Action>
            </Card.Header>
            <Card.Content class="flex items-center gap-6 text-sm">
                <div class="flex flex-col">
                    <span class="text-muted-foreground text-xs uppercase">Role</span>
                    <span class="font-medium">{state.master ? 'Master' : 'Follower'}</span>
                </div>
                <Separator orientation="vertical" class="h-8" />
                <div class="flex flex-col">
                    <span class="text-muted-foreground text-xs uppercase">Nodes</span>
                    <span class="font-medium">{state.nodes.length}</span>
                </div>
                <Separator orientation="vertical" class="h-8" />
                <div class="flex min-w-0 flex-col">
                    <span class="text-muted-foreground text-xs uppercase">Master</span>
                    <span class="truncate font-mono text-xs font-medium">
                        {state.nodes.find((node) => node.master)?.name ?? 'unknown'}
                    </span>
                </div>
            </Card.Content>
        </Card.Root>

        {#each state.warnings as warning (warning)}
            <Alert.Root variant="warning">
                <TriangleAlert />
                <Alert.Title>Framerate mismatch</Alert.Title>
                <Alert.Description>{warning}</Alert.Description>
            </Alert.Root>
        {/each}

        {#if !state.connected}
            <Alert.Root variant="destructive">
                <Unplug />
                <Alert.Title>Not connected to the cluster</Alert.Title>
                <Alert.Description>
                    Clustering is enabled but the channel is down, so this node is running on its
                    own clock. Check the network and the seed node configuration.
                </Alert.Description>
            </Alert.Root>
        {/if}

        <div class="grid grid-cols-1 gap-4 lg:grid-cols-2 xl:grid-cols-3">
            {#each state.nodes as node (node.id)}
                <ClusterNodeCard {node} />
            {/each}
        </div>
    </div>
{/if}
