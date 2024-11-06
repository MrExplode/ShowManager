<script lang="ts">
    import OutputPanel from '@/output/OutputPanel.svelte'
    import TimePanel from '@/time/TimePanel.svelte'
    import AudioPanel from '@/audio/AudioPanel.svelte'
    import SchedulerPanel from '@/scheduler/SchedulerPanel.svelte'
</script>

<div class="flex flex-wrap items-start justify-start gap-2">
    <TimePanel />
    <OutputPanel />
    <SchedulerPanel />
    <AudioPanel />
</div>
