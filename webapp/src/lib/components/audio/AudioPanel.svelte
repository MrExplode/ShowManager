<script lang="ts">
    import * as Card from '@/ui/card'
    import { Slider } from '@/ui/slider'
    import Volume from '@lucide/svelte/icons/volume'
    import Volume1 from '@lucide/svelte/icons/volume-1'
    import Volume2 from '@lucide/svelte/icons/volume-2'
    import Disc3 from '@lucide/svelte/icons/disc-3'

    import { loadedAudio, playing, volume, availableTracks } from '$lib/data/audio'
    import DataTable from '@/DataTable.svelte'
    import { columns } from '@/audio/track_columns'
    import AudioWaveform from '@/audio/AudioWaveform.svelte'

    let loadedName = $derived(
        $loadedAudio == null ? 'No track loaded' : `Loaded: ${$loadedAudio.path}`
    )
</script>

<Card.Root class="h-full">
    <Card.Header>
        <Card.Title>Audio player</Card.Title>
        <Card.Description>{loadedName}</Card.Description>
        <Card.Action>
            {#if $playing}
                <Disc3 class="text-muted-foreground animate-spin" />
            {/if}
        </Card.Action>
    </Card.Header>
    <Card.Content class="flex flex-col gap-4">
        <div class="flex items-center gap-6">
            {#if $volume[0] < 25}
                <Volume class="w-10" />
            {:else if $volume[0] < 50}
                <Volume1 class="w-10" />
            {:else}
                <Volume2 class="w-10" />
            {/if}
            <Slider
                type="multiple"
                class="min-w-32"
                min={0}
                max={100}
                step={1}
                value={$volume}
                onValueCommit={(v: number[]) => ($volume = v)}
                disabled={$loadedAudio == null}
            />
            <p class="w-10 text-right font-mono text-xl tabular-nums">{$volume}</p>
        </div>
        <AudioWaveform />
        <DataTable data={$availableTracks} {columns} />
    </Card.Content>
</Card.Root>
