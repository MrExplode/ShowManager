<script lang="ts">
    import * as Card from '@/ui/card'

    import {
        artNetOutput,
        audioOutput,
        ltcOutput,
        oscOutput,
        schedulerActive
    } from '$lib/data/output'
    import OutputSwitch from '@/output/OutputSwitch.svelte'
    import { playing } from '$lib/data/control'
    import { cluster } from '$lib/data/cluster'

    let clustered = $derived($cluster.enabled)
</script>

<Card.Root class="h-full">
    <Card.Header>
        <Card.Title>Output</Card.Title>
        <Card.Description>
            {clustered
                ? 'Arms outputs across the whole cluster; the badge shows which node drives each one'
                : 'Toggle available outputs'}
        </Card.Description>
    </Card.Header>
    <Card.Content class="flex flex-col gap-3">
        <OutputSwitch
            disabled={$playing}
            outputName="ArtNet"
            description="Timecode over ArtNet"
            store={artNetOutput}
            type="ARTNET"
        />
        <OutputSwitch
            disabled={$playing}
            outputName="Audio"
            description="Audio player output"
            store={audioOutput}
            type="AUDIO"
        />
        <OutputSwitch
            disabled={$playing}
            outputName="LTC"
            description="LTC timecode output"
            store={ltcOutput}
            type="LTC"
        />
        <OutputSwitch
            disabled={$playing}
            outputName="OSC"
            description="Outgoing OSC packets"
            store={oscOutput}
            type="OSC"
        />
        <OutputSwitch
            disabled={$playing}
            outputName="Scheduler"
            description="Scheduled events"
            store={schedulerActive}
        />
    </Card.Content>
</Card.Root>
