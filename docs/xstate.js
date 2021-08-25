// https://xstate.js.org/viz/

// Available variables:
// - Machine
// - interpret
// - assign
// - send
// - sendParent
// - spawn
// - raise
// - actions
// - XState (all XState exports)

const machine = Machine({
    id: 'transit',
    initial: 'draft',
    context: {
        retries: 0
    },
    states: {
        draft: {
            on: {
                PUBLISH: 'waiting_for_driver_acceptance'
            }
        },
        waiting_for_driver_acceptance: {
            on: {
                CANCEL: 'cancelled',
                ACCEPT: 'transit_to_passenger',
                FAILED: 'driver_assignment_failed'
            }
        },
        transit_to_passenger: {
            on: {
                CANCEL: 'cancelled',
                START_TRANSIT: 'in_transit'
            }
        },
        in_transit: {
            on: {
                COMPLETE: 'completed'
            }
        },
        cancelled: {
            type: 'final'
        },
        completed: {
            type: 'final'
        },
        driver_assignment_failed: {
            type: 'final'
        }
    }
});
