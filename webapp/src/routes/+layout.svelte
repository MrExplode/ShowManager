<script lang="ts">
    import { ModeWatcher } from 'mode-watcher'
    import { Toaster } from '@/ui/sonner'
    import '../app.css'
    let { children } = $props()
</script>

<ModeWatcher defaultMode="dark" />
<Toaster position="bottom-right" />
{@render children()}
