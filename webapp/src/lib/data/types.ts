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
    type: string
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
