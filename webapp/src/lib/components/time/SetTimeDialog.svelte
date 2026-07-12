<script lang="ts">
    import { Button, buttonVariants } from '@/ui/button'
    import * as Dialog from '@/ui/dialog'
    import * as Field from '@/ui/field'
    import { Input } from '@/ui/input'
    import { toast } from 'svelte-sonner'
    import { fromString } from '$lib/data/types'
    import { playing, setTime } from '$lib/data/control'
    import { cn } from '$utils'

    let { disabled, class: classNames = '' }: { disabled: boolean; class?: string } = $props()

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
        class={cn(buttonVariants({ variant: 'outline' }), classNames)}
    >
        Set
    </Dialog.Trigger>
    <Dialog.Content>
        <Dialog.Header>
            <Dialog.Title>Set time</Dialog.Title>
            <Dialog.Description>Jump the show clock to a timecode.</Dialog.Description>
        </Dialog.Header>
        <Field.FieldGroup>
            <Field.Field>
                <Field.FieldLabel for="timecode">Timecode</Field.FieldLabel>
                <Input id="timecode" bind:value={time} placeholder="00:00:00/00" />
                <Field.FieldDescription>Format: hh:mm:ss/ff</Field.FieldDescription>
            </Field.Field>
        </Field.FieldGroup>
        <Dialog.Footer>
            <Button {disabled} onclick={() => onSet()}>Set</Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
