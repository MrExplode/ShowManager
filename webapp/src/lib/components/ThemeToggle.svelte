<script lang="ts">
    import Sun from '@lucide/svelte/icons/sun'
    import Moon from '@lucide/svelte/icons/moon'
    import { toggleMode } from 'mode-watcher'
    import { Button } from '@/ui/button'
</script>

<Button onclick={toggleMode} variant="ghost" size="icon" aria-label="Toggle theme">
    <Sun class="hidden dark:block" />
    <Moon class="block dark:hidden" />
</Button>
