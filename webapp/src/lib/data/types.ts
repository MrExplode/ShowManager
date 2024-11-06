export default interface Marker {
    label: string
    time: string
}

export interface Timecode {
    hour: number
    min: number
    sec: number
    frame: number
    millisecLength: number
}

const timecodeRegex = /(?<hour>\d{2}):(?<min>\d{2}):(?<sec>\d{2})\/(?<frame>\d{2})/

export const fromString = (raw: string): Timecode | null => {
    const res = raw.match(timecodeRegex)
    if (!res) return null

    return {
        hour: parseInt(res!.groups['hour']),
        min: parseInt(res!.groups['min']),
        sec: parseInt(res!.groups['sec']),
        frame: parseInt(res!.groups['frame']),
        millisecLength: 0
    }
}

export const formatTime = (
    t: Timecode,
    pad: boolean = true,
    spaces: boolean = false,
    frames: boolean = false
): string => {
    const h = pad ? (t.hour < 10 ? '0' + t.hour.toString() : t.hour.toString()) : t.hour.toString()
    const m = pad ? (t.min < 10 ? '0' + t.min.toString() : t.min.toString()) : t.min.toString()
    const s = pad ? (t.sec < 10 ? '0' + t.sec.toString() : t.sec.toString()) : t.sec.toString()
    const f = pad
        ? t.frame < 10
            ? '0' + t.frame.toString()
            : t.frame.toString()
        : t.frame.toString()
    if (spaces) {
        if (frames) return `${h} : ${m} : ${s} / ${f}`
        else return `${h} : ${m} : ${s}`
    } else {
        if (frames) return `${h}:${m}:${s}/${f}`
        else return `${h}:${m}:${s}`
    }
}

export interface AudioMarker {
    label: string
    time: Timecode
}

export interface AudioTrack {
    start: Timecode
    volume: number
    markers: AudioMarker[]
    path: string
}

export interface Message {
    type: 'init' | 'time' | 'audio' | 'scheduler' | 'log' | 'output'
}

export interface InitMessage {
    type: 'init'
    logs: string[]
}

export interface TimeMessage {
    type: 'time'
    action: 'change' | 'start' | 'pause' | 'stop'
    hour: string | undefined
    min: string | undefined
    sec: string | undefined
    frame: string | undefined
}

export interface AudioMessage {
    type: 'audio'
    action: 'load' | 'start' | 'pause' | 'stop' | 'volume' | 'marker'
    volume: number | undefined
}

export interface SchedulerMessage {
    type: 'scheduler'
    action: 'record' | 'eventAdd' | 'eventExecuted' | 'syncEvents'
    record: boolean | undefined
    event: unknown | undefined
}

export interface LogMessage {
    type: 'log'
    log: string
}

export interface OutputMessage {
    type: 'output'
    name: 'artnet' | 'audio' | 'ltc' | 'scheduler'
    value: boolean
}

export interface ScheduledEvent {
    type: ScheduledEventType
    time: Timecode
}

export interface ScheduledJumpEvent extends ScheduledEvent {
    jumpTime: Timecode
}

export interface ScheduledOscEvent extends ScheduledEvent {
    packet: {
        address: string
        parameterType: string
        parameter: string
    }
}

export type ScheduledEventType = 'osc' | 'jump' | 'start' | 'stop'

export const eventTypes: ScheduledEventType[] = ['osc', 'jump', 'start', 'stop']
