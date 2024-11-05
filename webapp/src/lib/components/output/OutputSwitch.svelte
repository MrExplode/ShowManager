<script lang="ts">
    import { Switch } from '@/ui/switch'
    import { Label } from '@/ui/label'
    import * as Tooltip from '@/ui/tooltip'
    import type { Writable } from 'svelte/store'

    let {
        outputName,
        description,
        store,
        disabled
    }: { outputName: string; description: string; store: Writable<boolean>; disabled: boolean } =
        $props()
</script>

<Tooltip.Provider>
    <Tooltip.Root>
        <Tooltip.Trigger class="flex items-center space-x-2">
            <Switch bind:checked={$store} {disabled} />
            <Label>{outputName}</Label>
        </Tooltip.Trigger>
        <Tooltip.Content>
            <p>{description}</p>
        </Tooltip.Content>
    </Tooltip.Root>
</Tooltip.Provider>
