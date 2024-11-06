<script lang="ts">
    import { addEvent } from '$lib/data/scheduler'
    import {
        eventTypes,
        fromString,
        type ScheduledEvent,
        type ScheduledEventType,
        type ScheduledJumpEvent,
        type ScheduledOscEvent
    } from '$lib/data/types'
    import { cn } from '$utils'
    import { Button, buttonVariants } from '@/ui/button'
    import * as Dialog from '@/ui/dialog'
    import { Input } from '@/ui/input'
    import * as Select from '@/ui/select'
    import { Separator } from '@/ui/separator'
    import * as Tooltip from '@/ui/tooltip'
    import { toast } from 'svelte-sonner'

    let { class: classNames = '' }: { class: string } = $props()

    let isOpen = $state(false)
    let eventType = $state<ScheduledEventType | ''>('')
    let eventTrigger = $derived(eventType == '' ? 'Select a type' : 'Type: ' + eventType)
    let time = $state('')
    let timecode = $derived(fromString(time))
    let jumpTime = $state('')
    let jumpTimecode = $derived(fromString(jumpTime))

    let oscType = $state('')
    let oscTrigger = $derived(oscType == '' ? 'Select a type' : 'Type: ' + oscType)
    let oscAddress = $state('')
    let oscParam = $state('')

    const submit = () => {
        if (timecode == null) {
            toast.error('Invalid time format!', {
                description: 'Valid example: 00:00:00/00'
            })
            return
        }

        if (eventType == '') {
            toast.error('Please select a type!')
            return
        }

        let event: ScheduledEvent = {
            time: timecode,
            type: eventType
        }

        if (eventType == 'jump') {
            if (jumpTimecode == null) {
                toast.error('Invalid jump time format!', {
                    description: 'Valid example: 00:00:00/00'
                })
                return
            }
            ;(event as ScheduledJumpEvent).jumpTime = jumpTimecode
        }

        if (eventType == 'osc') {
            if (oscType == '') {
                toast.error('Please select an OSC data type!')
                return
            }

            let oscEvent = event as ScheduledOscEvent
            oscEvent.packet = {
                address: oscAddress,
                parameter: oscParam,
                parameterType: oscType
            }
        }

        addEvent(event).then(() => (isOpen = false))
    }
</script>

<Tooltip.Provider>
    <Dialog.Root bind:open={isOpen}>
        <Dialog.Trigger>
            <Tooltip.Root>
                <Tooltip.Trigger class={cn(buttonVariants({ variant: 'secondary' }), classNames)}
                    >New</Tooltip.Trigger
                >
                <Tooltip.Content>
                    <p>Create a new scheduled event</p>
                </Tooltip.Content>
            </Tooltip.Root>
        </Dialog.Trigger>
        <Dialog.Content>
            <Dialog.Title>Create event</Dialog.Title>
            <Dialog.Description>Create a new scheduled event</Dialog.Description>
            <Input bind:value={time} placeholder="Execute time" />
            <Select.Root type="single" bind:value={eventType}>
                <Select.Trigger>{eventTrigger}</Select.Trigger>
                <Select.Content>
                    <Select.Group>
                        <Select.GroupHeading>Type</Select.GroupHeading>
                        {#each eventTypes as t}
                            <Select.Item value={t}>{t}</Select.Item>
                        {/each}
                    </Select.Group>
                </Select.Content>
            </Select.Root>
            {#if eventType == 'osc'}
                <Separator class="my-2" />
                <Input placeholder="OSC Address" bind:value={oscAddress} />
                <Select.Root type="single" bind:value={oscType}>
                    <Select.Trigger>{oscTrigger}</Select.Trigger>
                    <Select.Content>
                        <Select.Group>
                            <Select.GroupHeading>OSC parameter type</Select.GroupHeading>
                            <Select.Item value="INTEGER">Integer</Select.Item>
                            <Select.Item value="FLOAT">Float</Select.Item>
                            <Select.Item value="BOOLEAN">Boolean</Select.Item>
                            <Select.Item value="STRING">String</Select.Item>
                            <Select.Item value="EMPTY">Empty</Select.Item>
                        </Select.Group>
                    </Select.Content>
                </Select.Root>
                <Input placeholder="OSC Parameter" bind:value={oscParam} />
            {:else if eventType == 'jump'}
                <Separator class="my-2" />
                <Input bind:value={jumpTime} placeholder="Jump to..." />
            {/if}
            <Dialog.Footer>
                <Button onclick={() => submit()}>Create</Button>
            </Dialog.Footer>
        </Dialog.Content>
    </Dialog.Root>
</Tooltip.Provider>
