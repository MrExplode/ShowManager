import type { ColumnDef } from '@tanstack/table-core'
import { formatTime, type AudioTrack } from '$lib/data/types'

export const columns: ColumnDef<AudioTrack>[] = [
    {
        accessorKey: 'startTime',
        header: 'Start',
        accessorFn: (track) => formatTime(track.startTime)
    },
    {
        accessorKey: 'file',
        header: 'File',
        accessorFn: (track) => track.file.path
    },
    {
        accessorKey: 'volume',
        header: 'Volume',
        accessorFn: (track) => track.volume * 100
    }
]
