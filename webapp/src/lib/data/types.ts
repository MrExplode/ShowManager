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
    const groups = raw.match(timecodeRegex)?.groups
    if (!groups) return null

    return {
        hour: parseInt(groups.hour),
        min: parseInt(groups.min),
        sec: parseInt(groups.sec),
        frame: parseInt(groups.frame),
        millisecLength: 0
    }
}

export const formatTime = (
    t: Timecode,
    config: { pad?: boolean; spaces?: boolean; frames?: boolean }
): string => {
    const h = config.pad
        ? t.hour < 10
            ? '0' + t.hour.toString()
            : t.hour.toString()
        : t.hour.toString()
    const m = config.pad
        ? t.min < 10
            ? '0' + t.min.toString()
            : t.min.toString()
        : t.min.toString()
    const s = config.pad
        ? t.sec < 10
            ? '0' + t.sec.toString()
            : t.sec.toString()
        : t.sec.toString()
    const f = config.pad
        ? t.frame < 10
            ? '0' + t.frame.toString()
            : t.frame.toString()
        : t.frame.toString()
    if (config.spaces) {
        if (config.frames) return `${h} : ${m} : ${s} / ${f}`
        else return `${h} : ${m} : ${s}`
    } else {
        if (config.frames) return `${h}:${m}:${s}/${f}`
        else return `${h}:${m}:${s}`
    }
}

export interface AudioMarker {
    label: string
    time: Timecode
}

export interface AudioTrack {
    start: Timecode
    end?: Timecode
    volume: number
    markers: AudioMarker[]
    path: string
}

export interface Message {
    type: 'init' | 'time' | 'audio' | 'scheduler' | 'log' | 'output' | 'cluster'
}

export interface InitMessage {
    type: 'init'
    logs: LogEntry[]
    cluster: ClusterState
}

export const logLevels = ['ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE'] as const
export type LogLevel = (typeof logLevels)[number]

export interface LogEntry {
    id: number
    time: string
    level: LogLevel
    thread: string
    logger: string
    message: string
    throwable?: string
}

export interface ClusterNode {
    id: string
    name: string
    /** the node serving this UI */
    self: boolean
    master: boolean
    framerate: number
    playing: boolean
    /** followers only: whether the offset estimator has a usable sample yet */
    synced: boolean
    /** follower clock -> master clock correction */
    offsetUs: number
    /** one-way network delay to the master */
    delayUs: number
    inView: boolean
    /** in the view, but has stopped reporting in */
    stale: boolean
    outputs: string[]
}

export interface ClusterState {
    enabled: boolean
    connected: boolean
    master: boolean
    clusterName: string
    useMulticast: boolean
    self: string
    coordinator: string | null
    framerate: number
    nodes: ClusterNode[]
    warnings: string[]
}

export interface TimeMessage {
    type: 'time'
    action: 'change' | 'start' | 'pause' | 'stop'
    time: Timecode
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
    event: ScheduledEvent | undefined
}

export interface LogMessage {
    type: 'log'
    entry: LogEntry
}

export interface ClusterMessage {
    type: 'cluster'
    state: ClusterState
}

export interface OutputMessage {
    type: 'output'
    name: 'artnet' | 'audio' | 'ltc' | 'scheduler'
    value: boolean
}

export interface ScheduledEvent {
    type: ScheduledEventType
    time: Timecode
    id?: string
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
