<script lang="ts">
    import {
        formatTime,
        type ScheduledEvent,
        type ScheduledJumpEvent,
        type ScheduledOscEvent
    } from '$lib/data/types'
    import { Badge } from '@/ui/badge'
    import * as Tooltip from '@/ui/tooltip'

    let { event }: { event: ScheduledEvent } = $props()
    let packet = $derived(event.type == 'osc' ? (event as ScheduledOscEvent).packet : null)
</script>

{#if event.type == 'osc'}
    <div class="space-y-1">
        <p>{packet?.address}</p>
        <p>{packet?.parameterType}</p>
        <p>{packet?.parameter}</p>
    </div>
{:else if event.type == 'jump'}
    <Tooltip.Provider>
        <Tooltip.Root>
            <Tooltip.Trigger>
                <Badge>{formatTime((event as ScheduledJumpEvent).jumpTime, { pad: true })}</Badge>
            </Tooltip.Trigger>
            <Tooltip.Content>
                <p>here</p>
            </Tooltip.Content>
        </Tooltip.Root>
    </Tooltip.Provider>
{/if}
