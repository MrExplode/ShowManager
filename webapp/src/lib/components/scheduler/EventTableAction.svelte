<script lang="ts">
    import Ellipsis from '@lucide/svelte/icons/ellipsis'
    import CircleX from '@lucide/svelte/icons/circle-x'
    import { Button } from '@/ui/button'
    import * as Dropdown from '@/ui/dropdown-menu'
    import type { ScheduledEvent } from '$lib/data/types'
    import { deleteEvents } from '$lib/data/scheduler'

    // todo - hold event id
    let { event }: { event: ScheduledEvent } = $props()
</script>

<Dropdown.Root>
    <Dropdown.Trigger>
        {#snippet child({ props })}
            <Button {...props} variant="ghost" size="icon" class="relative size-8 p-0">
                <Ellipsis class="size-4" />
            </Button>
        {/snippet}
    </Dropdown.Trigger>
    <Dropdown.Content>
        <Dropdown.Group>
            <Dropdown.GroupHeading>Actions</Dropdown.GroupHeading>
            <Dropdown.Item
                onclick={() => {
                    console.log('BOOOO delete!!!', event.id)
                    deleteEvents([event]).then(() => console.log('delete completed'))
                }}
            >
                <CircleX class="mr-2 size-4" />
                <p>Delete event</p>
            </Dropdown.Item>
        </Dropdown.Group>
    </Dropdown.Content>
</Dropdown.Root>
