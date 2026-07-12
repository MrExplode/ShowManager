<script lang="ts">
    import { currentTime, playing, paused, play, pause, stop, quickJump } from '$lib/data/control'
    import { cluster } from '$lib/data/cluster'

    import Play from '@lucide/svelte/icons/play'
    import Pause from '@lucide/svelte/icons/pause'
    import Square from '@lucide/svelte/icons/square'
    import Rewind from '@lucide/svelte/icons/rewind'
    import FastForward from '@lucide/svelte/icons/fast-forward'
    import { Button } from '@/ui/button'
    import * as Card from '@/ui/card'
    import { Badge } from '@/ui/badge'
    import SetTimeDialog from './SetTimeDialog.svelte'

    const pad = (n: number) => n.toString().padStart(2, '0')

    let clock = $derived(
        `${pad($currentTime.hour)}:${pad($currentTime.min)}:${pad($currentTime.sec)}`
    )
    let frames = $derived(pad($currentTime.frame))
    let follower = $derived($cluster.enabled && !$cluster.master)
</script>

<Card.Root class="h-full">
    <Card.Header>
        <Card.Title>Transport</Card.Title>
        <Card.Description>
            {follower ? 'Chasing the master clock' : 'This node owns the show clock'}
        </Card.Description>
        <Card.Action>
            {#if $playing}
                <Badge variant="success">Playing</Badge>
            {:else if $paused}
                <Badge variant="warning">Paused</Badge>
            {:else}
                <Badge variant="outline">Stopped</Badge>
            {/if}
        </Card.Action>
    </Card.Header>

    <Card.Content class="flex flex-col gap-6">
        <div class="flex items-baseline gap-2 tabular-nums">
            <span class="font-mono text-5xl font-semibold tracking-tight sm:text-6xl">
                {clock}
            </span>
            <span class="text-muted-foreground font-mono text-2xl">/{frames}</span>
        </div>

        <div class="flex flex-wrap gap-2">
            <Button onclick={play} disabled={$playing}>
                <Play data-icon="inline-start" />
                Play
            </Button>
            <Button variant="secondary" onclick={pause} disabled={!$playing}>
                <Pause data-icon="inline-start" />
                Pause
            </Button>
            <Button variant="secondary" onclick={stop} disabled={!$playing && !$paused}>
                <Square data-icon="inline-start" />
                Stop
            </Button>
            <SetTimeDialog disabled={$playing} class="ml-2" />
        </div>
    </Card.Content>

    <Card.Footer class="flex flex-wrap gap-2">
        <Button variant="outline" size="sm" onclick={() => quickJump(-10)}>
            <Rewind data-icon="inline-start" />
            10s
        </Button>
        <Button variant="outline" size="sm" onclick={() => quickJump(-1)}>
            <Rewind data-icon="inline-start" />
            1s
        </Button>
        <Button variant="outline" size="sm" onclick={() => quickJump(1)}>
            <FastForward data-icon="inline-start" />
            1s
        </Button>
        <Button variant="outline" size="sm" onclick={() => quickJump(10)}>
            <FastForward data-icon="inline-start" />
            10s
        </Button>
    </Card.Footer>
</Card.Root>
