<script lang="ts">
    import { Button, buttonVariants } from '@/ui/button'
    import * as Dialog from '@/ui/dialog'
    import { Input } from '@/ui/input'
    import { toast } from 'svelte-sonner'
    import { fromString } from '$lib/data/types'
    import { playing, setTime } from '$lib/data/control'
    import { cn } from '$utils'

    let { disabled, class: classNames = '' }: { disabled: boolean; class: string } = $props()

    let time = $state('')
    let isOpen = $state(false)

    const onSet = () => {
        const timecode = fromString(time)
        if (timecode == null) {
            toast.error('Invalid time format!', {
                description: 'Valid example: 00:00:00/00'
            })
            return
        }

        setTime(timecode).then(() => (isOpen = false))
    }
</script>

<Dialog.Root bind:open={isOpen}>
    <Dialog.Trigger
        disabled={$playing}
        class={cn(buttonVariants({ variant: 'outline-solid' }), classNames)}>Set</Dialog.Trigger
    >
    <Dialog.Content>
        <Dialog.Header>
            <Dialog.Title>Set time</Dialog.Title>
            <Dialog.Description>Set the time immediately</Dialog.Description>
        </Dialog.Header>
        <Input bind:value={time} />
        <Dialog.Footer>
            <Button {disabled} onclick={() => onSet()}>Set</Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
