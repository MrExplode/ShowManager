<script lang="ts">
    import * as Card from '@/ui/card'

    import { artNetOutput, audioOutput, ltcOutput, schedulerActive } from '$lib/data/output'
    import OutputSwitch from '@/output/OutputSwitch.svelte'
</script>

<Card.Root class="m-5">
    <Card.Header>
        <Card.Title>Output</Card.Title>
        <Card.Description>Toggle available outputs</Card.Description>
    </Card.Header>
    <Card.Content class="flex flex-col space-y-2">
        <OutputSwitch outputName="ArtNet" description="Timecode over ArtNet" store={artNetOutput} />
        <OutputSwitch outputName="Audio" description="Audio player output" store={audioOutput} />
        <OutputSwitch outputName="LTC" description="LTC timecode output" store={ltcOutput} />
        <OutputSwitch
            outputName="Scheduler"
            description="Scheduled events"
            store={schedulerActive}
        />
    </Card.Content>
</Card.Root>
