<script lang="ts">
    import LoadingScreen from '@/LoadingScreen.svelte'
    import { connected } from '$lib/data/control'
</script>

{#if $connected}
    <p>connected</p>
{:else}
    <LoadingScreen />
{/if}
