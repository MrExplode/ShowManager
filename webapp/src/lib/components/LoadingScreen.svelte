<script lang="ts">
    import * as Empty from '@/ui/empty'
    import { Spinner } from '@/ui/spinner'
    import { retryCountdown } from '$lib/data/control'
</script>

<div class="flex min-h-screen items-center justify-center p-6">
    <Empty.Root class="max-w-md border">
        <Empty.Header>
            <Empty.Media variant="icon">
                <Spinner />
            </Empty.Media>
            <Empty.Title class="tracking-[0.2em] uppercase">ShowManager</Empty.Title>
            <Empty.Description>
                Connecting to the backend… retrying in {$retryCountdown}s
            </Empty.Description>
        </Empty.Header>
    </Empty.Root>
</div>
