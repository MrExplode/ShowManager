<script lang="ts">
    import LoadingScreen from '@/LoadingScreen.svelte'
    import { connected } from '$lib/data/control'
    import OutputPanel from '@/output/OutputPanel.svelte'
</script>

{#if $connected}
    <div class="flex">
        <OutputPanel />
    </div>
{:else}
    <LoadingScreen />
{/if}
