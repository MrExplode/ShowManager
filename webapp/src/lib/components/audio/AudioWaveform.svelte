<script lang="ts">
    import Peaks, { type PeaksOptions } from 'peaks.js'
    import { loadedAudio } from '$lib/data/audio'
    import LoaderCircle from 'lucide-svelte/icons/loader-circle'
    import { player } from '@/audio/waveform'

    let zoomview: HTMLElement | null = null
    let overview: HTMLElement | null = null

    const loadWaveData = async (): Promise<void> => {
        const res = await fetch('http://localhost:7000/audio/samples')
        if (res.status != 200) throw new Error('Request failed')
        const data = await res.arrayBuffer()

        const peaksOptions: PeaksOptions = {
            zoomview: {
                container: zoomview
            },
            overview: {
                container: overview
            },
            player: player,
            waveformData: {
                arraybuffer: data
            }
        }

        Peaks.init(peaksOptions, (e) => console.log('Peaks init failed:', e))
    }
</script>

{#if $loadedAudio != ''}
    <div class="space-y-2">
        <div bind:this={zoomview} class="h-[100px] w-[1000px] border shadow-sm"></div>
        <div bind:this={overview} class="h-[75px] w-[1000px] border shadow-sm"></div>
    </div>
    {#await loadWaveData()}
        <div class="space-y-3">
            <LoaderCircle class="h-10 w-10 animate-spin" />
            <p>Loading...</p>
        </div>
    {:catch}
        <p>Failed to load waveform render.</p>
    {/await}
{/if}
