<script lang="ts">
    import { Switch } from '@/ui/switch'
    import { Label } from '@/ui/label'
    import { Badge } from '@/ui/badge'
    import * as Tooltip from '@/ui/tooltip'
    import type { Writable } from 'svelte/store'

    let {
        outputName,
        description,
        store,
        disabled,
        owned = true,
        clustered = false
    }: {
        outputName: string
        description: string
        store: Writable<boolean>
        disabled: boolean
        owned?: boolean
        clustered?: boolean
    } = $props()

    let unowned = $derived(clustered && !owned)
</script>

<div class="flex items-center justify-between gap-4">
    <Tooltip.Provider>
        <Tooltip.Root>
            <Tooltip.Trigger class="flex items-center gap-2">
                <Switch bind:checked={$store} {disabled} />
                <Label>{outputName}</Label>
            </Tooltip.Trigger>
            <Tooltip.Content>
                <p>{description}</p>
                {#if unowned}
                    <p class="text-muted-foreground">
                        This node is not assigned to drive {outputName}, so nothing leaves it even
                        while enabled.
                    </p>
                {/if}
            </Tooltip.Content>
        </Tooltip.Root>
    </Tooltip.Provider>

    {#if unowned}
        <Badge variant="outline">not driven here</Badge>
    {/if}
</div>
