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

<Card.Root class="m-2">
    <Card.Header class="flex flex-row items-start justify-between">
        <div class="flex flex-col space-y-1.5">
            <Card.Title>Audio player</Card.Title>
            <Card.Description>{loadedName}</Card.Description>
        </div>
        {#if $playing}
            <Disc3 class="text-muted-foreground h-7 animate-spin" />
        {/if}
    </Card.Header>
    <Card.Content class="flex-1 flex-col items-center space-y-4 text-center">
        <div class="flex items-center space-x-6">
            {#if $volume[0] < 25}
                <Volume class="w-10" />
            {:else if $volume[0] < 50}
                <Volume1 class="w-10" />
            {:else}
                <Volume2 class="w-10" />
            {/if}
            <Slider
                class="min-w-32"
                min={0}
                max={100}
                step={1}
                value={$volume}
                onValueCommit={(v) => ($volume = v)}
                disabled={$loadedAudio == null}
            />
            <p class="text-bold text-xl">{$volume}</p>
        </div>
        <AudioWaveform />
        <DataTable data={$availableTracks} {columns} />
    </Card.Content>
</Card.Root>
