<script lang="ts">
    import OutputPanel from '@/output/OutputPanel.svelte'
    import TimePanel from '@/time/TimePanel.svelte'
    import AudioPanel from '@/audio/AudioPanel.svelte'
    import SchedulerPanel from '@/scheduler/SchedulerPanel.svelte'
</script>

<div class="grid grid-cols-1 gap-4 xl:grid-cols-3">
    <div class="xl:col-span-2">
        <TimePanel />
    </div>
    <div>
        <OutputPanel />
    </div>
    <div class="xl:col-span-2">
        <AudioPanel />
    </div>
    <div>
        <SchedulerPanel />
    </div>
</div>
