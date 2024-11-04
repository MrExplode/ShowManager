<script lang="ts">
    import { currentTime, playing, paused, play, pause, stop } from '$lib/data/control'

    import Disc3 from 'lucide-svelte/icons/disc-3'
    import { Button } from '@/ui/button'
    import * as Card from '@/ui/card'
</script>

<Card.Root class="m-2">
    <Card.Header class="flex flex-row items-start justify-between">
        <div class="flex flex-col space-y-1.5">
            <Card.Title>Current time</Card.Title>
            <Card.Description>{$currentTime}</Card.Description>
        </div>
        {#if $playing}
            <Disc3 class="h-7 animate-spin text-muted-foreground" />
        {/if}
    </Card.Header>
    <Card.Footer class="mt-4 flex gap-2">
        <Button disabled={$playing} onclick={play}>Play</Button>
        <Button disabled={$paused} onclick={pause}>Pause</Button>
        <Button onclick={stop}>Stop</Button>
        <Button disabled={$playing} class="ml-4" variant="outline">Set</Button>
    </Card.Footer>
</Card.Root>
