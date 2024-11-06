import { renderComponent, renderSnippet } from '@/ui/data-table'
import { formatTime, type ScheduledEvent } from '$lib/data/types'
import type { ColumnDef } from '@tanstack/table-core'
import { createRawSnippet } from 'svelte'
import EventTableAction from './EventTableAction.svelte'
import EventDetail from './EventDetail.svelte'
import ExecutionStatus from './ExecutionStatus.svelte'

export const columns: ColumnDef<ScheduledEvent>[] = [
    {
        accessorKey: 'time',
        accessorFn: (event) => formatTime(event.time),
        header: () => {
            const amountHeaderSnippet = createRawSnippet(() => ({
                render: () => `<div class="text-right">Time</div>`
            }))
            return renderSnippet(amountHeaderSnippet, '')
        }
    },
    {
        accessorKey: 'type',
        header: 'Type'
    },
    {
        id: 'details',
        cell: ({ row }) => renderComponent(EventDetail, { event: row.original })
    },
    {
        id: 'executionStatus',
        cell: ({ row }) => renderComponent(ExecutionStatus, { event: row.original })
    },
    {
        id: 'actions',
        cell: ({ row }) => renderComponent(EventTableAction, { event: row.original })
    }
]
