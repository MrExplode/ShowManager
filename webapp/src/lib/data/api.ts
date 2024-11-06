import { dev } from '$app/environment'

const BASE = dev ? 'http://localhost:7000' : ''

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const get = async (path: string): Promise<any> => {
    const response = await fetch(BASE + path, {
        method: 'get'
    })
    return await response.json()
}

export const post = async (path: string, body: unknown | undefined = undefined) => {
    await fetch(BASE + path, {
        method: 'post',
        body: body == undefined ? undefined : JSON.stringify(body)
    })
}

export const wait = (p: Promise<unknown>) => {
    p.then(() => {})
}
