<script lang="ts">
    import * as Card from '@/ui/card'

    import { artNetOutput, audioOutput, ltcOutput, schedulerActive } from '$lib/data/output'
    import OutputSwitch from '@/output/OutputSwitch.svelte'
    import { playing } from '$lib/data/control'
</script>

<Card.Root class="m-2">
    <Card.Header>
        <Card.Title>Output</Card.Title>
        <Card.Description>Toggle available outputs</Card.Description>
    </Card.Header>
    <Card.Content class="flex flex-col space-y-2">
        <OutputSwitch
            disabled={$playing}
            outputName="ArtNet"
            description="Timecode over ArtNet"
            store={artNetOutput}
        />
        <OutputSwitch
            disabled={$playing}
            outputName="Audio"
            description="Audio player output"
            store={audioOutput}
        />
        <OutputSwitch
            disabled={$playing}
            outputName="LTC"
            description="LTC timecode output"
            store={ltcOutput}
        />
        <OutputSwitch
            disabled={$playing}
            outputName="Scheduler"
            description="Scheduled events"
            store={schedulerActive}
        />
    </Card.Content>
</Card.Root>
