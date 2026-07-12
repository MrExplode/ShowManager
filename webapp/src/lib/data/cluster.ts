import { derived, writable } from 'svelte/store'
import { get } from '$lib/data/api'
import type { ClusterState } from '$lib/data/types'

const standalone: ClusterState = {
    enabled: false,
    connected: false,
    master: true,
    clusterName: '',
    useMulticast: true,
    self: 'local',
    coordinator: null,
    framerate: 25,
    nodes: [],
    warnings: []
}

export const cluster = writable<ClusterState>(standalone)

export const selfNode = derived(cluster, ($cluster) => $cluster.nodes.find((node) => node.self))

/** Always true when clustering is off. */
export const isMaster = derived(cluster, ($cluster) => $cluster.master)

export const setCluster = (state: ClusterState) => cluster.set(state)

export const syncCluster = async () => {
    cluster.set(await get('/cluster/state'))
}
