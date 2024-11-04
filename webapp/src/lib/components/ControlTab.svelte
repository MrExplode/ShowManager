<script lang="ts">
    import OutputPanel from '@/output/OutputPanel.svelte'
    import TimePanel from '@/time/TimePanel.svelte'
    import AudioPanel from '@/audio/AudioPanel.svelte'
</script>

<div class="flex items-start justify-start gap-4">
    <TimePanel />
    <OutputPanel />
    <AudioPanel />
</div>
