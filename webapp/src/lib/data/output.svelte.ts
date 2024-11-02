import { get, post, wait } from './api'

let isSyncing = false
let artNetOutput = $state(false)
let audioOutput = $state(false)
let ltcOutput = $state(false)
let schedulerActive = $state(false)

$effect(() => {
    if (!isSyncing) {
        wait(
            post('/output/artnet', {
                enabled: artNetOutput
            })
        )
    }
})

$effect(() => {
    if (!isSyncing) {
        wait(
            post('/output/audio', {
                enabled: audioOutput
            })
        )
    }
})

$effect(() => {
    if (!isSyncing) {
        wait(
            post('/output/ltc', {
                enabled: ltcOutput
            })
        )
    }
})

$effect(() => {
    if (!isSyncing) {
        wait(
            post('/output/scheduler', {
                enabled: schedulerActive
            })
        )
    }
})

const syncOutputs = async () => {
    const data = await get('/output/all')
    isSyncing = true
    artNetOutput = data.artnet
    audioOutput = data.audio
    ltcOutput = data.ltc
    schedulerActive = data.scheduler
    isSyncing = false
}

export default { artNetOutput, audioOutput, ltcOutput, schedulerActive, syncOutputs }
