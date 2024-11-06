import { renderComponent, renderSnippet } from '@/ui/data-table'
import { formatTime, type ScheduledEvent } from '$lib/data/types'
import type { ColumnDef } from '@tanstack/table-core'
import { createRawSnippet } from 'svelte'
import EventTableAction from './EventTableAction.svelte'

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
        id: 'actions',
        cell: ({ row }) => {
            return renderComponent(EventTableAction, { event: row.original })
        }
    }
]
