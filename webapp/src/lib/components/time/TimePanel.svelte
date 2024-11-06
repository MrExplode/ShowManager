<script lang="ts">
    import { currentTime, playing, paused, play, pause, stop } from '$lib/data/control'

    import Disc3 from 'lucide-svelte/icons/disc-3'
    import { Button } from '@/ui/button'
    import * as Card from '@/ui/card'
    import SetTimeDialog from './SetTimeDialog.svelte'
    import { formatTime } from '$lib/data/types'
</script>

<Card.Root class="m-2">
    <Card.Header class="flex flex-row items-start justify-between">
        <div class="flex flex-col space-y-1.5">
            <Card.Title>Current time</Card.Title>
            <Card.Description
                >{formatTime($currentTime, {
                    pad: true,
                    spaces: true,
                    frames: true
                })}</Card.Description
            >
        </div>
        {#if $playing}
            <Disc3 class="h-7 animate-spin text-muted-foreground" />
        {/if}
    </Card.Header>
    <Card.Footer class="mt-4 flex gap-2">
        <Button disabled={$playing} onclick={play}>Play</Button>
        <Button disabled={$paused} onclick={pause}>Pause</Button>
        <Button onclick={stop}>Stop</Button>
        <SetTimeDialog disabled={$playing} class="ml-4" />
    </Card.Footer>
</Card.Root>
