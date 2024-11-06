import type {
    AudioMessage,
    InitMessage,
    LogMessage,
    Message,
    OutputMessage,
    SchedulerMessage,
    TimeMessage
} from '$lib/data/types'
import {
    playing as audioPlaying,
    syncAudio,
    syncMarkers,
    volume,
    setSyncing as audioSyncing
} from '$lib/data/audio'
import {
    connected,
    currentTime,
    paused as controlPaused,
    playing as controlPlaying,
    syncPlaying,
    loadLogs,
    addLog
} from '$lib/data/control'
import {
    artNetOutput,
    audioOutput,
    ltcOutput,
    setSyncing as outputSyncing,
    schedulerActive,
    syncOutputs
} from '$lib/data/output'
import { executedIds, syncEvents, syncRecording } from '$lib/data/scheduler'
import { get } from 'svelte/store'

export const handle = async (msg: Message) => {
    switch (msg.type) {
        case 'init':
            await handleInit(msg as InitMessage)
            break
        case 'time':
            await handleTime(msg as TimeMessage)
            break
        case 'audio':
            await handleAudio(msg as AudioMessage)
            break
        case 'scheduler':
            await handleScheduler(msg as SchedulerMessage)
            break
        case 'log':
            handleLog(msg as LogMessage)
            break
        case 'output':
            handleOutput(msg as OutputMessage)
            break
    }
}

const handleInit = async (msg: InitMessage) => {
    await syncPlaying()
    await syncAudio()
    await syncOutputs()
    await syncRecording()
    await syncEvents()
    loadLogs(msg.logs)
    connected.set(true)
}

const handleTime = async (msg: TimeMessage) => {
    switch (msg.action) {
        case 'change':
            currentTime.set(`${msg.hour} : ${msg.min} : ${msg.sec} / ${msg.frame}`)
            break
        case 'start':
            controlPlaying.set(true)
            controlPaused.set(false)
            break
        case 'pause':
            controlPlaying.set(false)
            controlPaused.set(true)
            break
        case 'stop':
            controlPlaying.set(false)
            controlPaused.set(false)
            await syncEvents()
            break
    }
}

const handleAudio = async (msg: AudioMessage) => {
    switch (msg.action) {
        case 'load':
            await syncAudio()
            break
        case 'start':
            audioPlaying.set(true)
            break
        case 'pause':
            break
        case 'stop':
            audioPlaying.set(false)
            break
        case 'volume':
            audioSyncing(true)
            volume.set([msg.volume as number])
            audioSyncing(false)
            break
        case 'marker':
            await syncMarkers()
            break
    }
}

const handleScheduler = async (msg: SchedulerMessage) => {
    switch (msg.action) {
        case 'record':
            // deprecated feature anyway
            break
        case 'eventAdd':
            // could add directly, but I don't wanna do array ops on svelte stores
            await syncEvents()
            break
        case 'eventExecuted':
            // todo
            executedIds.set([...get(executedIds), msg.event!.id])
            break
        case 'syncEvents':
            await syncEvents()
            break
    }
}

const handleLog = (msg: LogMessage) => {
    addLog(msg.log)
}

const handleOutput = (msg: OutputMessage) => {
    outputSyncing(true)
    switch (msg.name) {
        case 'artnet':
            artNetOutput.set(msg.value)
            break
        case 'audio':
            audioOutput.set(msg.value)
            break
        case 'ltc':
            ltcOutput.set(msg.value)
            break
        case 'scheduler':
            schedulerActive.set(msg.value)
            break
    }
    outputSyncing(false)
}
