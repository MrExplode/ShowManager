<script lang="ts">
    import { Button, buttonVariants } from '@/ui/button'
    import * as Dialog from '@/ui/dialog'
    import { Input } from '@/ui/input'
    import { toast } from 'svelte-sonner'
    import type { Timecode } from '$lib/data/types'
    import { playing, setTime } from '$lib/data/control'
    import { cn } from '$utils'

    const format = /(?<hour>\d{2}):(?<min>\d{2}):(?<sec>\d{2})\/(?<frame>\d{2})/

    let { disabled, class: classNames }: { disabled: boolean; class: string } = $props()

    let time = $state('')
    let isOpen = $state(false)

    const onSet = () => {
        const res = time.match(format)
        if (res == null) {
            toast.error('Invalid time format!', {
                description: 'Valid example: 00:00:00/00'
            })
        }

        const timecode: Timecode = {
            hour: parseInt(res!.groups['hour']),
            min: parseInt(res!.groups['min']),
            sec: parseInt(res!.groups['sec']),
            frame: parseInt(res!.groups['frame']),
            millisecLength: 0
        }

        setTime(timecode).then(() => (isOpen = false))
    }
</script>

<Dialog.Root bind:open={isOpen}>
    <Dialog.Trigger
        disabled={$playing}
        class={cn(buttonVariants({ variant: 'outline' }), classNames)}>Set</Dialog.Trigger
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
