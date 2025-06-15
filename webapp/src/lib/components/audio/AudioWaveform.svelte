<script lang="ts">
    import Peaks, { type PeaksInstance, type PeaksOptions } from 'peaks.js'
    import { loadedAudio } from '$lib/data/audio'
    import LoaderCircle from 'lucide-svelte/icons/loader-circle'
    import ZoomIn from 'lucide-svelte/icons/zoom-in'
    import ZoomOut from 'lucide-svelte/icons/zoom-out'
    import Expand from 'lucide-svelte/icons/expand'
    import Shrink from 'lucide-svelte/icons/shrink'
    import { player } from '@/audio/waveform'
    import { Button } from '@/ui/button'
    import { dev } from '$app/environment'

    const URL = dev ? 'http://localhost:7000/audio/samples' : '/audio/samples'

    let zoomview: HTMLElement | null = null
    let scrollbar: HTMLElement | null = null
    let overview: HTMLElement | null = null

    let peaks: PeaksInstance | null = null

    let amplitudeIndex = 5
    let amplitudes = [0.0, 0.1, 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0, 5.0]

    const loadWaveData = async (): Promise<void> => {
        const res = await fetch(URL)
        if (res.status != 200) throw new Error('Request failed')
        const data = await res.json()

        const peaksOptions: PeaksOptions = {
            zoomview: {
                container: zoomview,
                autoScroll: true
            },
            scrollbar: {
                container: scrollbar!,
                minWidth: 100
            },
            overview: {
                container: overview
            },
            player: player,
            waveformData: {
                json: data
            }
        }

        Peaks.init(peaksOptions, (e, instance) => {
            console.log('e:', e)
            console.log('instance:', instance)
            if (e) {
                console.log('Peaks init failed:', e)
            } else {
                peaks = instance!
            }
            console.log('peaks', peaks)
        })
    }

    const amplitude = (direction: number) => {
        amplitudeIndex = Math.min(Math.max(amplitudeIndex + direction, 0), 10)
        console.log('set amplitude to ', amplitudes[amplitudeIndex])
        peaks?.views.getView('zoomview')?.setAmplitudeScale(amplitudes[amplitudeIndex])
    }
</script>

{#if $loadedAudio != null}
    <div class="flex">
        <div class="mr-2 flex flex-col gap-2">
            <Button size="icon" variant="ghost" onclick={() => amplitude(1)}
                ><ZoomIn class="size-4" /></Button
            >
            <Button size="icon" variant="ghost" onclick={() => amplitude(-1)}
                ><ZoomOut class="size-4" /></Button
            >
            <Button size="icon" variant="ghost" onclick={() => zoom(-1)}
                ><Expand class="size-4" /></Button
            >
            <Button size="icon" variant="ghost" onclick={() => zoom(-1)}
                ><Shrink class="size-4" /></Button
            >
        </div>
        <div class="space-y-2">
            <div class="flex h-[100px] w-[1000px] flex-col border shadow-xs">
                <div class="size-full" bind:this={zoomview}></div>
                <div class="h-4 w-full" bind:this={scrollbar}></div>
            </div>
            <div bind:this={overview} class="h-[75px] w-[1000px] border shadow-xs"></div>
        </div>
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
