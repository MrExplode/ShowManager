<script lang="ts">
    import { connected } from '$lib/data/control'
    import LoadingScreen from '@/LoadingScreen.svelte'
    import LandingScreen from '@/LandingScreen.svelte'
</script>

{#if $connected}
    <LandingScreen />
{:else}
    <LoadingScreen />
{/if}
