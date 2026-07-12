<script lang="ts">
    import * as Card from '@/ui/card'
    import DataTable from '$components/DataTable.svelte'
    import { events } from '$lib/data/scheduler'
    import { columns } from '@/scheduler/event_columns'
    import CreateEventDialog from './CreateEventDialog.svelte'
    import { ScrollArea } from '$components/ui/scroll-area'
</script>

<Card.Root class="h-full">
    <Card.Header>
        <Card.Title>Scheduler</Card.Title>
        <Card.Description>Events fired against the show clock</Card.Description>
        <Card.Action>
            <CreateEventDialog />
        </Card.Action>
    </Card.Header>
    <Card.Content>
        <ScrollArea class="h-64">
            <DataTable data={$events} {columns} />
        </ScrollArea>
    </Card.Content>
</Card.Root>
