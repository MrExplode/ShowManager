<script lang="ts">
    import * as Tabs from '@/ui/tabs'
    import ControlTab from '@/ControlTab.svelte'
</script>

<div class="flex flex-col space-y-4 p-8 pt-6">
    <h1 class="text-3xl font-bold tracking-tight">ShowManager</h1>
    <Tabs.Root value="control">
        <Tabs.List>
            <Tabs.Trigger value="control">Control</Tabs.Trigger>
            <Tabs.Trigger value="logs">Logs</Tabs.Trigger>
        </Tabs.List>
        <Tabs.Content value="control">
            <ControlTab />
        </Tabs.Content>
        <Tabs.Content value="logs">
            <p>logs</p>
        </Tabs.Content>
    </Tabs.Root>
</div>
