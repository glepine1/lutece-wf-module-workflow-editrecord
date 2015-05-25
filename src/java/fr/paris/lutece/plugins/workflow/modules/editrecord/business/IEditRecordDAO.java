/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.editrecord.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 *
 * IEditRecordDAO
 *
 */
public interface IEditRecordDAO
{
    /**
     * Insert new record
     * @param editRecord the EditRecord Object
     * @param plugin the plugin
     */
    void insert( EditRecord editRecord, Plugin plugin );

    /**
     * Insert new record
     * @param editRecord the EditRecord Object
     * @param plugin the plugin
     */
    void store( EditRecord editRecord, Plugin plugin );

    /**
     * Load a EditRecord by id history
     * @param nIdHistory the id history
     * @param nIdTask the task id
     * @param plugin the plugin
     * @return EditRecord Object
     */
    EditRecord load( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Load a list of EditRecord by id task
     * @param nIdTask the id task
     * @param plugin the plugin
     * @return a list of EditRecord
     */
    List<EditRecord> loadByIdTask( int nIdTask, Plugin plugin );

    /**
     * Remove EditRecord by id history
     * @param nIdHistory the id history
     * @param nIdTask the task id
     * @param plugin the plugin
     */
    void deleteByIdHistory( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Remove EditRecord by id task
     * @param nIdTask the task id
     * @param plugin the plugin
     */
    void deleteByIdTask( int nIdTask, Plugin plugin );
}
