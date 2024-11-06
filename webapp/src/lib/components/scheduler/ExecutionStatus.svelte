<script lang="ts">
    import { executedIds } from '$lib/data/scheduler'
    import type { ScheduledEvent } from '$lib/data/types'
    import { Badge } from '@/ui/badge'

    let { event }: { event: ScheduledEvent } = $props()
    let executed = $derived($executedIds.includes(event.id!))
</script>

{#if executed}
    <Badge class="bg-emerald-500 dark:bg-emerald-900">Executed</Badge>
{/if}
