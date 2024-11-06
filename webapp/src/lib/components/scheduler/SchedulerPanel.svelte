<script lang="ts">
    import * as Card from '@/ui/card'
    import { Button } from '@/ui/button'
    import DataTable from '$components/DataTable.svelte'
    import { events } from '$lib/data/scheduler'
    import { columns } from '@/scheduler/event_columns'
    import CreateEventDialog from './CreateEventDialog.svelte'
</script>

<Card.Root class="m-2 self-start">
    <Card.Header class="flex flex-row items-center justify-between px-6 pb-0 pt-3">
        <Card.Title>Scheduler</Card.Title>
        <!-- <Button class="ml-6" variant="secondary">New</Button> -->
        <CreateEventDialog class="ml-6" />
    </Card.Header>
    <Card.Content>
        <DataTable data={$events} {columns} />
    </Card.Content>
</Card.Root>
