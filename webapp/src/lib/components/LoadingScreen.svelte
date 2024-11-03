<script lang="ts">
    import * as Card from '@/ui/card'
    import LoaderPinwheel from 'lucide-svelte/icons/loader-pinwheel'
</script>

<div class="flex min-h-screen items-center justify-center">
    <Card.Root class="text-center shadow-lg">
        <Card.Content class="space-y-3">
            <LoaderPinwheel class="m-10 h-32 w-32 animate-spin" />
            <h1 class="text-xl font-bold tracking-tight">ShowManager</h1>
            <p>Connecting...</p>
        </Card.Content>
    </Card.Root>
</div>
