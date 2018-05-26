import React from "react";
import {Table} from "react-bootstrap";

class ScheduleRow extends React.Component {
    render() {
        const name = this.props.name;
        const value = this.props.value;

        return (
            <tr>
                <td>{name}</td>
                <td>{value}</td>
            </tr>
        );
    }
}

class ScheduleTable extends React.Component {
    render() {
        const rows = [];

        if (this.props.gameStarted) {
            rows.push(
                <ScheduleRow
                    name={"Current Time"}
                    value={this.props.currentTime}
                    key={"currentTime"}/>
            );
        }

        Object.keys(this.props.schedule).forEach((name) => {
            rows.push(
                <ScheduleRow
                    name={name}
                    value={this.props.schedule[name]}
                    key={name}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Event</th>
                    <th>Time</th>
                </tr>
                </thead>
                <tbody>
                {rows}
                </tbody>
            </Table>
        );
    }
}

export default ScheduleTable;