<script lang="ts">
    import * as Card from '@/ui/card'

    import { artNetOutput, audioOutput, ltcOutput, schedulerActive } from '$lib/data/output'
    import OutputSwitch from '@/output/OutputSwitch.svelte'
    import { playing } from '$lib/data/control'
    import { cluster, selfNode } from '$lib/data/cluster'

    let clustered = $derived($cluster.enabled)
    let owned = $derived($selfNode?.outputs ?? [])
</script>

<Card.Root class="h-full">
    <Card.Header>
        <Card.Title>Output</Card.Title>
        <Card.Description>
            {clustered ? 'Outputs this node is assigned to drive' : 'Toggle available outputs'}
        </Card.Description>
    </Card.Header>
    <Card.Content class="flex flex-col gap-3">
        <OutputSwitch
            disabled={$playing}
            outputName="ArtNet"
            description="Timecode over ArtNet"
            store={artNetOutput}
            owned={owned.includes('ARTNET')}
            {clustered}
        />
        <OutputSwitch
            disabled={$playing}
            outputName="Audio"
            description="Audio player output"
            store={audioOutput}
            owned={owned.includes('AUDIO')}
            {clustered}
        />
        <OutputSwitch
            disabled={$playing}
            outputName="LTC"
            description="LTC timecode output"
            store={ltcOutput}
            owned={owned.includes('LTC')}
            {clustered}
        />
        <OutputSwitch
            disabled={$playing}
            outputName="Scheduler"
            description="Scheduled events"
            store={schedulerActive}
        />
    </Card.Content>
</Card.Root>
