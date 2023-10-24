#!/usr/bin/env tarantool

local log = require('log')
local uuid = require('uuid')

local function init()
    local dialogue_space = box.schema.space.create('dialogue', {
        if_not_exists = true
    })

    dialogue_space:format({
        {name = 'userId', type = 'string'},
        {name = 'messageCount', type = 'number'}
    })

    dialogue_space:create_index("pk", {parts = {'userId'}, if_not_exists = true})

end

function add_data(userId)
    local dialogue_space = box.space.dialogue
    local t =  box.space.dialogue.index.pk:pairs({userId}):nth(1)
    if t == nil
    then
        dialogue_space:insert{userId, 1}
    else
        dialogue_space:update(userId, {{'+', 2, 1}})
    end
end

function minus_data(userId)
    local dialogue_space = box.space.dialogue
    local t =  box.space.dialogue.index.pk:pairs({userId}):nth(1)
    if t == nil
    then
        dialogue_space:insert{userId, 0}
    else
        dialogue_space:update(userId, {{'-', 2, 1}})
    end
end

function get_data(userId)
    return box.space.dialogue.index.pk:select(userId)
end

box.cfg
{
    pid_file = nil,
    background = false,
    log_level = 5
}

box.once('init', init)