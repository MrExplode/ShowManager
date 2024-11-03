<script lang="ts">
    import LoadingScreen from '@/LoadingScreen.svelte'
    import Button from '$components/ui/button/button.svelte'
    import { connected } from '$lib/data/control'
</script>

{#if $connected}
    <div class="flex min-h-screen items-center justify-center">
        <Button
            onclick={() => {
                $connected = false
            }}>Disconnect</Button
        >
    </div>
{:else}
    <LoadingScreen />
{/if}
