/*
 * OnDemand - Attach javascript events on dynamic content - Inspired by Drupal Behaviours
 * Copyright (C) 2014 James Bodkin
 * See https://github.com/LonelyStorm/jquery-ondemand
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
;(function(window, document, $, undefined) {

    'use strict';

    window.OnDemand = {};

    $.OnDemandAttach = function(context, options) {
        context = context || document;

        $.each(OnDemand, function() {
            if ($.isFunction(this.attach)) {
                this.attach(context, options);
            }
        });
    };

    $(document).ready(function() {
        $.OnDemandAttach();
    });

}(window, document, jQuery));
