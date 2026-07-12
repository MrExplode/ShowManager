<script lang="ts">
    import { Switch } from '@/ui/switch'
    import { Label } from '@/ui/label'
    import { Badge } from '@/ui/badge'
    import * as Tooltip from '@/ui/tooltip'
    import type { Writable } from 'svelte/store'
    import type { OutputType } from '$lib/data/types'
    import { cluster } from '$lib/data/cluster'

    let {
        outputName,
        description,
        store,
        disabled,
        type
    }: {
        outputName: string
        description: string
        store: Writable<boolean>
        disabled: boolean
        /** omit for outputs that every node runs, like the scheduler */
        type?: OutputType
    } = $props()

    let clustered = $derived($cluster.enabled)
    let drivers = $derived(type ? $cluster.nodes.filter((node) => node.outputs.includes(type)) : [])
    let drivenHere = $derived(drivers.some((node) => node.self))
    let elsewhere = $derived(drivers.filter((node) => !node.self).map((node) => node.name))
</script>

<div class="flex items-center justify-between gap-4">
    <Tooltip.Provider>
        <Tooltip.Root>
            <Tooltip.Trigger class="flex items-center gap-2">
                <Switch bind:checked={$store} {disabled} />
                <Label>{outputName}</Label>
            </Tooltip.Trigger>
            <Tooltip.Content class="max-w-64">
                <p>{description}</p>
                {#if clustered}
                    <p class="text-muted-foreground">
                        Arms {outputName} on every node in the cluster.
                        {#if !type}
                            Runs on every node; what it sends out is still gated by each node's own
                            outputs.
                        {:else if drivenHere}
                            This node drives it.
                        {:else if elsewhere.length > 0}
                            Driven by {elsewhere.join(', ')}, not by this node.
                        {:else}
                            No node is assigned to drive it, so nothing will come out anywhere.
                        {/if}
                    </p>
                {/if}
            </Tooltip.Content>
        </Tooltip.Root>
    </Tooltip.Provider>

    {#if clustered}
        {#if !type}
            <Badge variant="outline">every node</Badge>
        {:else if drivenHere}
            <Badge variant="success">driven here</Badge>
        {:else if elsewhere.length > 0}
            <Badge variant="outline">{elsewhere.join(', ')}</Badge>
        {:else}
            <Badge variant="warning">unassigned</Badge>
        {/if}
    {/if}
</div>
