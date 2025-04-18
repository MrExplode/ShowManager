import { dev } from '$app/environment'
import { get } from 'svelte/store'
import type { Message } from '$lib/data/types'
import { connected, retryCountdown } from '$lib/data/control'
import { handle } from '$lib/data/message_handler'

let socket: WebSocket | null = null
let pingTaskId = -1
let retryTaskId = -1

export const load = () => {
    socket = new WebSocket(dev ? 'ws://localhost:7000' : '/')
    socket.onopen = open
    socket.onclose = close
    socket.onmessage = message
}

const open = () => {
    console.log('[WS] Connected')
    pingTaskId = setInterval(() => socket?.send(JSON.stringify({ type: 'ping' })), 10000)
}

const close = (event: CloseEvent) => {
    console.log('[WS] Disconnected: ', event.reason)
    connected.set(false)
    if (pingTaskId != -1) clearInterval(pingTaskId)

    retryConnection()
}

const message = (event: MessageEvent) => {
    try {
        const payload = JSON.parse(event.data) as Message
        handle(payload).catch((e) => console.log('Failed to handle WS message', e))
    } catch (error: unknown) {
        console.log('Failed to handle WS message', error)
    }
}

const retryConnection = () => {
    retryCountdown.set(5)
    retryTaskId = setInterval(() => {
        const count = get(retryCountdown)
        if (count > 0) {
            retryCountdown.set(count - 1)
        } else {
            clearInterval(retryTaskId)
            socket = null
            load()
        }
    }, 1000)
}
