<script lang="ts">
    import { Toaster } from '@/ui/sonner'
    import '../app.css'
    let { children } = $props()
</script>

<Toaster />
{@render children()}
