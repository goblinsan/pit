import React from "react";
import {Button, ControlLabel, FormControl, FormGroup, Panel} from "react-bootstrap";

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: "",
        };
        this.validateForm = this.validateForm.bind(this);
        this.onChange = this.onChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    validateForm() {
        return this.state.username.length > 0 && this.state.password.length > 0;
    }

    onChange = (e) => {
        this.setState({[e.target.id]: e.target.value});
    };

    handleSubmit = event => {
        let currentComponent = this;
        event.preventDefault();
        let formData = new FormData();
        formData.append("username", this.state.username);
        formData.append("password", this.state.password);
        fetch('http://localhost:8080/login', {
            method: 'POST',
            body: formData,
            credentials: 'include'
        }).then(function(res) {
            if (res.ok) {
                currentComponent.props.onLogin(res.headers.get('usertoken'));
                // console.log(res.headers);
                currentComponent.props.history.push('/dashboard')
            } else if (res.status === 401) {
                alert("Oops! You are not authorized.");
            }
        }, function(e) {
            alert("Error submitting form!");
        });
    };

    render() {
        return (
            <Panel>
                <Panel.Body>
                    <div id={"formForLogin"}>
                        <form onSubmit={this.handleSubmit}>
                            <FormGroup controlId="username" bsSize="large">
                                <ControlLabel>User</ControlLabel>
                                <FormControl
                                    autoFocus
                                    type="text"
                                    value={this.state.username}
                                    onChange={this.onChange}
                                />
                            </FormGroup>
                            <FormGroup controlId="password" bsSize="large">
                                <ControlLabel>Password</ControlLabel>
                                <FormControl
                                    value={this.state.password}
                                    onChange={this.onChange}
                                    type="password"
                                />
                            </FormGroup>
                            <Button
                                block
                                bsSize="large"
                                disabled={!this.validateForm}
                                type="submit"
                            >
                                Login
                            </Button>
                        </form>
                    </div>
                </Panel.Body>
            </Panel>
        );
    }
}

export default Login;