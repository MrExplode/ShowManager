import type { ColumnDef } from '@tanstack/table-core'
import { formatTime, type AudioTrack } from '$lib/data/types'

export const columns: ColumnDef<AudioTrack>[] = [
    {
        accessorKey: 'startTime',
        header: 'Start',
        accessorFn: (track) => formatTime(track.start, { pad: true, frames: true })
    },
    {
        accessorKey: 'file',
        header: 'File',
        accessorFn: (track) => track.path
    },
    {
        accessorKey: 'volume',
        header: 'Volume',
        accessorFn: (track) => track.volume * 100
    }
]
